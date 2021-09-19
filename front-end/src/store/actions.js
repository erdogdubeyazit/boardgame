import userService from '@/services/user'
import gameService from '@/services/game'

export const logout = ({ commit }) => {
  commit('logout')
}

export const getMyData = ({ commit }) => {
  userService.getMyData().then(data => {
    commit('updateMyData', data)
  })
}

export const getGames = ({ commit }) => {
  gameService.getGames().then(data => {
    commit('updateGames', data)
  })
}

export const addUserGame = ({ commit }, game) => {
  commit('addUserGame', game)
}

export const removeUserGame = ({ commit }, index) => {
  commit('removeUserGame', index)
}

export const addAvailableGame = ({ commit }, game, index) => {
  commit('addAvailableGame', game, index)
}

export const removeAvailableGame = ({ commit }, index) => {
  commit('removeAvailableGame', index)
}
