import Vue from 'vue'
import SockJS from 'sockjs-client'
import eventBus from '@/event-bus'

class WebSocketClient {
  constructor () {
    this.serverUrl = null
    this.token = null
    this.socket = null
    this.authenticated = false
    this.loggedOut = false
    this.$bus = new Vue()
    this.subscribeQueue = {}
    this.unsubscribeQueue = {}
  }

  init (serverUrl, token) {
    if (this.authenticated) {
      console.warn('[WebSocket] Already authenticated.')
      return
    }
    console.log('[WebSocket] Initializing')
    this.serverUrl = serverUrl
    this.token = token
    this.connect()
  }

  logout () {
    console.log('[WebSocket] Logging out')
    this.subscribeQueue = {}
    this.unsubscribeQueue = {}
    this.authenticated = false
    this.loggedOut = true
    this.socket && this.socket.close()
  }

  connect () {
    console.log('[WebSocket] Connecting to ' + this.serverUrl)
    this.socket = new SockJS(this.serverUrl + '?token=' + this.token)
    this.socket.onopen = () => {
      this.authenticated = true
      this._onConnected()
    }
    this.socket.onmessage = (event) => {
      this._onMessageReceived(event)
    }
    this.socket.onerror = (error) => {
      this._onSocketError(error)
    }
    this.socket.onclose = (event) => {
      this._onClosed(event)
    }
  }

  subscribe (channel, handler) {
    if (!this._isConnected()) {
      this._addToSubscribeQueue(channel, handler)
      return
    }
    const message = {
      action: 'subscribe',
      channel
    }
    this._send(message)
    this.$bus.$on(this._channelEvent(channel), handler)
    console.log('[WebSocket] Subscribed to channel ' + channel)
  }

  unsubscribe (channel, handler) {
    if (this.loggedOut) {
      return
    }

    if (!this._isConnected()) {
      this._addToUnsubscribeQueue(channel, handler)
      return
    }
    const message = {
      action: 'unsubscribe',
      channel
    }
    this._send(message)
    this.$bus.$off(this._channelEvent(channel), handler)
    console.log('[WebSocket] Unsubscribed from channel ' + channel)
  }

  _isConnected () {
    return this.socket && this.socket.readyState === SockJS.OPEN
  }

  _onConnected () {
    eventBus.$emit('WebSocketClient.connected')
    console.log('[WebSocket] Connected')

    this._processQueues()
  }

  _onMessageReceived (event) {
    const message = JSON.parse(event.data)
    console.log('[WebSocket] Received message', message)

    if (message.channel) {
      this.$bus.$emit(this._channelEvent(message.channel), JSON.parse(message.payload))
    }
  }

  _send (message) {
    this.socket.send(JSON.stringify(message))
  }

  _onSocketError (error) {
    console.error('[WebSocket] Socket error', error)
  }

  _onClosed (event) {
    console.log('[WebSocket] Received close event', event)
    if (this.loggedOut) {
      console.log('[WebSocket] Logged out')
      eventBus.$emit('WebSocketClient.loggedOut')
    } else {
      console.log('[WebSocket] Disconnected')
      eventBus.$emit('WebSocketClient.disconnected')

      setTimeout(() => {
        console.log('[WebSocket] Reconnecting')
        eventBus.$emit('WebSocketClient.reconnecting')
        this.connect()
      }, 1000)
    }
  }

  _channelEvent (channel) {
    return 'channel:' + channel
  }

  _processQueues () {
    console.log('[WebSocket] Processing subscribe/unsubscribe queues')

    const subscribeChannels = Object.keys(this.subscribeQueue)
    subscribeChannels.forEach(channel => {
      const handlers = this.subscribeQueue[channel]
      handlers.forEach(handler => {
        this.subscribe(channel, handler)
        this._removeFromQueue(this.subscribeQueue, channel, handler)
      })
    })

    const unsubscribeChannels = Object.keys(this.unsubscribeQueue)
    unsubscribeChannels.forEach(channel => {
      const handlers = this.unsubscribeQueue[channel]
      handlers.forEach(handler => {
        this.unsubscribe(channel, handler)
        this._removeFromQueue(this.unsubscribeQueue, channel, handler)
      })
    })
  }

  _addToSubscribeQueue (channel, handler) {
    console.log('[WebSocket] Adding channel subscribe to queue. Channel: ' + channel)
    this._removeFromQueue(this.unsubscribeQueue, channel, handler)
    const handlers = this.subscribeQueue[channel]
    if (!handlers) {
      this.subscribeQueue[channel] = [handler]
    } else {
      handlers.push(handler)
    }
  }

  _addToUnsubscribeQueue (channel, handler) {
    console.log('[WebSocket] Adding channel unsubscribe to queue. Channel: ' + channel)
    this._removeFromQueue(this.subscribeQueue, channel, handler)
    const handlers = this.unsubscribeQueue[channel]
    if (!handlers) {
      this.unsubscribeQueue[channel] = [handler]
    } else {
      handlers.push(handlers)
    }
  }

  _removeFromQueue (queue, channel, handler) {
    const handlers = queue[channel]
    if (handlers) {
      const index = handlers.indexOf(handler)
      if (index > -1) {
        handlers.splice(index, 1)
      }
    }
  }
}

export default new WebSocketClient()
