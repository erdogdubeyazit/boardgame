export default {
  updateMyData (state, data) {
    state.user = {}
    state.user.id = data.user.userId
    state.user.name = data.user.name
    state.user.authenticated = true
  },
  logout (state) {
    state.user = {}
    state.user.id = ''
    state.user.name = ''
    state.user.authenticated = false
  },
  updateGames (state, data) {
    state.myGames = data.myGames
    state.availableGames = data.availableGames
    state.activeGames = data.activeGames
  },
  addUserGame (state, data) {
    state.myGames.unshift(data)
  },
  addAvailableGame (state, data, index) {
    state.availableGames.splice(index, 0, data)
  },
  removeUserGame (state, index) {
    state.myGames.splice(index, 1)
  },
  removeAvailableGame (state, index) {
    state.availableGames.splice(index, 1)
  },
  removeActiveGame (state, index) {
    state.availableGames.splice(index, 1)
  }

}
