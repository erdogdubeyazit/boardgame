<template>
  <div class="h-100">
    <PageHeader />

    <div class="container toolbox">
      <div class="row">
        <div class="col-10">{{ gameInfo }}</div>
        <div v-if="playable" class="col-2 text-right">
          <button
            class="btn btn-danger btn-sm"
            type="button"
            @click="quitGame()"
          >
            {{ $t("gameScenePage.quit") }}
          </button>
        </div>
      </div>
    </div>

    <div v-if="playable" class="container scene">
      <div class="row h-75">
        <div class="col-2 pit d-flex align-items-center">
          <Tank :itemCount="oppositeTank" />
        </div>
        <div class="col-8 h-100">
          <div class="row h-50">
            <div
              class="col pit d-flex align-items-center"
              v-for="i in pitCount"
              :key="i"
            >
              <Pit :itemCount="oppositePits[pitCount - i]" />
            </div>
          </div>
          <div class="row h-50">
            <div
              class="col pit d-flex align-items-center"
              v-for="i in pitCount"
              :key="i"
            >
              <Pit
                :itemCount="ownPits[i - 1]"
                :pitIndex="i - 1"
                :playable="isMyTurn"
                @onPlay="handlePlay"
              />
            </div>
          </div>
        </div>
        <div class="col-2 pit d-flex align-items-center">
          <Tank :itemCount="ownTank" />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import PageHeader from '../components/PageHeader.vue'
import Tank from '../components/Tank.vue'
import Pit from '../components/Pit.vue'

import { mapGetters } from 'vuex'
import notify from '@/utils/notify'
import gameService from '@/services/game'

export default {
  components: { PageHeader, Tank, Pit },
  name: 'GameScene',
  computed: {
    ...mapGetters(['user']),
    isMyTurn: {
      get () {
        return this.gameSessionData.currentPlayer === this.user.id
      }
    },
    isGameOwner: {
      get () {
        return this.gameSessionData.playerA === this.user.id
      }
    },
    pitCount: {
      get () {
        return this.gameSessionData.pitCount
      }
    },
    ownTank: {
      get () {
        return this.isGameOwner
          ? this.gameSessionData.tankA
          : this.gameSessionData.tankB
      }
    },
    oppositeTank: {
      get () {
        return this.isGameOwner
          ? this.gameSessionData.tankB
          : this.gameSessionData.tankA
      }
    },
    ownPits: {
      get () {
        return this.isGameOwner
          ? this.gameSessionData.playerAPits
          : this.gameSessionData.playerBPits
      }
    },
    oppositePits: {
      get () {
        return this.isGameOwner
          ? this.gameSessionData.playerBPits
          : this.gameSessionData.playerAPits
      }
    }
  },
  data () {
    return {
      gameId: '',
      gameInfo: '',
      playable: true,
      gameSessionData: {}
    }
  },
  methods: {
    handlePlay (pitIndex) {
      gameService
        .play(this.gameId, pitIndex)
        .then((data) => {
          this.gameSessionData = data
        })
        .catch((error) => {
          notify.error(error.message)
        })
    },
    fetchGameData (gameId) {
      gameService
        .getGameData(gameId)
        .then((data) => {
          this.gameSessionData = data
          this.subscribeGameSceneChannel()
        })
        .catch((error) => {
          notify.error(error.message)
          this.playable = false
          this.gameInfo = this.$i18n.t(
            'gameScenePage.infoMessages.genericErrorMessage'
          )
        })
    },
    checkGameState (gameData) {
      if (
        !(
          gameData.gameStatus === 'STARTED' ||
          gameData.gameStatus === 'COMPLETED'
        )
      ) {
        this.playable = false
        this.gameInfo = this.$i18n.t(
          'gameScenePage.infoMessages.genericErrorMessage'
        )
      }
    },
    quitGame () {
      gameService
        .quitGame(this.gameId)
        .then(() => {
          // this.$store.dispatch('getGames')
          this.$router.push({ name: 'home' })
        })
        .catch((error) => {
          notify.error(error.message)
        })
    },
    handleGamePlayedNotification (payload) {
      if (
        payload.type === 'gamePlayed' &&
        (!this.gameSessionData.turn ||
          payload.gameData.turn > this.gameSessionData.turn)
      ) {
        this.gameSessionData = payload.gameData
      }
    },
    handlePlayerLeftNotification (payload) {
      if (
        payload.type === 'playerLeft' &&
        payload.gameData.gameId === this.gameId
      ) {
        this.$router.push({ name: 'home' })
      }
    },
    subscribeGameSceneChannel () {
      this.$webSocketClient.subscribe(
        '/game-scene/' + this.gameId,
        this.onGameSceneNotificationReceived
      )
    },
    unSubscribeGameSceneChannel () {
      this.$webSocketClient.unsubscribe(
        '/game-scene/' + this.gameId,
        this.onGameSceneNotificationReceived
      )
    },
    handleTurnInformationMessage (player) {
      if (player === this.user.id) {
        this.gameInfo = this.$i18n.t('gameScenePage.infoMessages.ownTurn')
      }

      if (player !== this.user.id) {
        this.gameInfo = this.$i18n.t('gameScenePage.infoMessages.opponentTurn')
      }
    },
    handleGameCompleteMessage (gameSessionData) {
      if (gameSessionData.gameStatus === 'COMPLETED') {
        if (!gameSessionData.winner) {
          this.gameInfo = this.$i18n.t('gameScenePage.infoMessages.tie')
        } else if (
          (gameSessionData.winner.player === 'A' && this.isGameOwner) ||
          (gameSessionData.winner.player === 'B' && !this.isGameOwner)
        ) {
          this.gameInfo = this.$i18n.t('gameScenePage.infoMessages.youWon', {
            point: this.gameSessionData.winner.points
          })
        } else {
          this.gameInfo = this.$i18n.t('gameScenePage.infoMessages.youLost')
        }
        // this.$store.dispatch('getGames')
        this.playable = false
      }
    },
    onGameSceneNotificationReceived (payload) {
      this.handleGamePlayedNotification(payload)
      this.handlePlayerLeftNotification(payload)
    }
  },
  mounted () {
    this.gameId = this.$route.query.game_id
    if (this.gameId) {
      this.fetchGameData(this.gameId)
    } else {
      this.playable = false
      this.gameInfo = this.$i18n.t(
        'gameScenePage.infoMessages.genericErrorMessage'
      )
    }
  },
  watch: {
    gameSessionData: {
      handler (newData) {
        this.handleTurnInformationMessage(newData.currentPlayer)

        this.handleGameCompleteMessage(newData)

        this.checkGameState(newData)
      },
      deep: true
    }
  },
  beforeDestroy () {
    this.unSubscribeGameSceneChannel()
  },
  beforeRouteLeave (to, from, next) {
    next()
    this.unSubscribeGameSceneChannel()
  }
}
</script>

<style lang="scss" scoped>
.scene {
  // margin-top: 1em;
  height: calc(100% - 200px);
}
.toolbox {
  margin-top: 1em;
}
</style>
