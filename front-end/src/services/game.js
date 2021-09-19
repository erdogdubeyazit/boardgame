import axios from 'axios'
import errorParser from '@/utils/error-parser'

export default {
  getGames () {
    return new Promise((resolve, reject) => {
      axios.get('/games/').then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        reject(errorParser.parse(error))
      })
    })
  },
  createGame (pitCount, itemsPerPit) {
    return new Promise((resolve, reject) => {
      axios.post('/games', { pitCount, itemsPerPit }).then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        reject(errorParser.parse(error))
      })
    })
  },
  play (gameId, pitIndex) {
    return new Promise((resolve, reject) => {
      axios.post('/games/' + gameId + '/play', { pitIndex }).then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        reject(errorParser.parse(error))
      })
    })
  },
  cancelGame (gameId) {
    return new Promise((resolve, reject) => {
      axios.patch('/games/' + gameId + '/cancel').then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        reject(errorParser.parse(error))
      })
    })
  },
  joinGame (gameId) {
    return new Promise((resolve, reject) => {
      axios.patch('/games/' + gameId).then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        reject(errorParser.parse(error))
      })
    })
  },
  getGameData (gameId) {
    return new Promise((resolve, reject) => {
      axios.get('/games/' + gameId).then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        reject(errorParser.parse(error))
      })
    })
  },
  quitGame (gameId) {
    return new Promise((resolve, reject) => {
      axios.patch('/games/' + gameId + '/quit').then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        reject(errorParser.parse(error))
      })
    })
  }
}
