<template>
  <div class="home">
    <page-header></page-header>
    <div class="jumbotron jumbotron-fluid">
      <div class="container">
        <h1 class="display-4">BEYAZIT</h1>
        <p class="lead">Demo application</p>
      </div>
    </div>
    <div class="container text-right">
      <button
        class="btn btn-primary btn-sm float-right"
        @click="createGame"
        type="button"
      >
        {{ $t("homePage.createGame") }}
      </button>
    </div>
    <div class="container">
      <h2>{{ $t("homePage.activeGames") }}</h2>
      <div
        v-if="activeGames.length === 0"
        class="alert alert-info"
        role="alert"
      >
        {{ $t("homePage.noGames") }}
      </div>
      <div v-else>
        <table class="table table-borderless table-sm table-hover">
          <thead>
            <tr>
              <th>{{ $t("homePage.pitCount") }}</th>
              <th>{{ $t("homePage.itemsPerPit") }}</th>
              <th></th>
            </tr>
          </thead>
          <tbody v-for="game in activeGames" :key="game.gameId">
            <tr class="align-middle">
              <td>{{ game.pitCount }}</td>
              <td>{{ game.itemsPerPit }}</td>
              <td class="text-right">
                <router-link
                  class="nav-link"
                  :to="{ name: 'gameScene', query: { game_id: game.gameId } }"
                  >{{ $t("homePage.continueGame") }}</router-link
                >
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <hr />
      <h2>{{ $t("homePage.availableGames") }}</h2>
      <div
        v-if="availableGames.length === 0"
        class="alert alert-info"
        role="alert"
      >
        {{ $t("homePage.noGames") }}
      </div>
      <div v-else>
        <table class="table table-borderless table-sm table-hover">
          <thead>
            <tr>
              <th>{{ $t("homePage.pitCount") }}</th>
              <th>{{ $t("homePage.itemsPerPit") }}</th>
              <th></th>
            </tr>
          </thead>
          <tbody v-for="game in availableGames" :key="game.gameId">
            <tr class="align-middle">
              <td>{{ game.pitCount }}</td>
              <td>{{ game.itemsPerPit }}</td>
              <td class="text-right">
                <button
                  class="btn btn-light btn-sm"
                  type="button"
                  @click="joinGame(game.gameId)"
                >
                  {{ $t("homePage.join") }}
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <hr />
      <h2>{{ $t("homePage.myGames") }}</h2>
      <div v-if="myGames.length === 0" class="alert alert-info" role="alert">
        {{ $t("homePage.noGames") }}
      </div>
      <div v-else>
        <table class="table table-borderless table-sm table-hover">
          <thead>
            <tr>
              <th>{{ $t("homePage.pitCount") }}</th>
              <th>{{ $t("homePage.itemsPerPit") }}</th>
              <th></th>
            </tr>
          </thead>
          <tbody v-for="(game, index) in myGames" :key="game.gameId">
            <tr class="align-middle">
              <td>{{ game.pitCount }}</td>
              <td>{{ game.itemsPerPit }}</td>
              <td class="text-right">
                <button
                  class="btn btn-danger btn-sm"
                  type="button"
                  @click="cancelGame(game.gameId, index)"
                >
                  {{ $t("homePage.cancel") }}
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    <CreateGameModal @gameSaved="onGameSaved" />
  </div>
</template>

<script>
import $ from 'jquery'
import notify from '@/utils/notify'
import PageHeader from '@/components/PageHeader.vue'
import CreateGameModal from '@/modals/CreateGameModal.vue'
import { mapGetters } from 'vuex'
import gameService from '@/services/game'

export default {
  name: 'Home',
  components: {
    PageHeader,
    CreateGameModal
  },
  computed: {
    ...mapGetters(['user', 'myGames', 'availableGames', 'activeGames'])
  },
  methods: {
    createGame () {
      $('#createGameModal').modal('show')
    },
    onGameSaved (game) {
      this.$store.dispatch('addUserGame', game)
    },
    cancelGame (gameId, index) {
      gameService
        .cancelGame(gameId)
        .then(() => {
          this.$store.dispatch('removeUserGame', index)
        })
        .catch((error) => {
          notify.error(error.message)
        })
    },
    joinGame (gameId) {
      gameService
        .joinGame(gameId)
        .then(() => {
          this.$router.push({ name: 'gameScene', query: { game_id: gameId } })
        })
        .catch((error) => {
          this.$store.dispatch('getGames')
          notify.error(error.message)
        })
    },
    findGamePositionInSortedArray (arr, game) {
      let start = 0
      let end = arr.length - 1

      while (start <= end) {
        const mid = Math.floor((start + end) / 2)

        if (arr[mid].gameId === game.gameId) return mid
        else if (arr[mid].sortingReference > game.sortingReference) {
          start = mid + 1
        } else end = mid - 1
      }

      return -1
    },
    subscribeGameLobbyChannel () {
      this.$webSocketClient.subscribe(
        '/game-lobby/',
        this.onGameLobbyNotificationReceived
      )
    },
    unSubscribeGameLobbyChannel () {
      this.$webSocketClient.unsubscribe(
        '/game-lobby/',
        this.onGameLobbyNotificationReceived
      )
    },
    onGameLobbyNotificationReceived (payload) {
      if (payload.type === 'gameCreated') {
        this.handleGameCreateNotification(payload.gameData)
      }
      if (payload.type === 'gameCancelled') {
        this.handleGameCancelNotification(payload.gameData)
      }
      if (payload.type === 'gameStarted') {
        this.handleGameStartedNotification(payload.gameData)
      }

      if (payload.type === 'playerLeft') {
        this.handlPlayerLeftGameNotification()
      }
    },
    handleGameCreateNotification (data) {
      if (
        this.user.id &&
        data.playerA !== this.user.id &&
        data.gameStatus === 'CREATED'
      ) {
        const game = {
          gameId: data.gameId,
          pitCount: data.pitCount,
          itemsPerPit: data.itemsPerPit,
          sortingReference: data.sortingReference
        }
        const index = this.findGamePositionInSortedArray(
          this.availableGames,
          game
        )

        this.$store.dispatch(
          'addAvailableGame',
          game,
          index === -1 ? 0 : index
        )
      }
    },
    handleGameCancelNotification (data) {
      if (data.playerA !== this.user.id && data.gameStatus === 'CANCELLED') {
        const game = {
          gameId: data.gameId,
          sortingReference: data.sortingReference
        }
        const index = this.findGamePositionInSortedArray(
          this.availableGames,
          game
        )
        this.$store.dispatch('removeAvailableGame', index === -1 ? 0 : index)
      }
    },
    handleGameStartedNotification (data) {
      if (data.playerA === this.user.id && data.gameStatus === 'STARTED') {
        this.$router.push({
          name: 'gameScene',
          query: { game_id: data.gameId }
        })
      } else if (
        data.playerB !== this.user.id &&
        data.gameStatus === 'STARTED'
      ) {
        const game = {
          gameId: data.gameId,
          sortingReference: data.sortingReference
        }
        const index = this.findGamePositionInSortedArray(
          this.availableGames,
          game
        )
        this.$store.dispatch('removeAvailableGame', index === -1 ? 0 : index)
      }
    },
    handlPlayerLeftGameNotification () {
      this.$store.dispatch('getGames')
    }
  },
  mounted () {
    this.subscribeGameLobbyChannel()
    this.$store.dispatch('getGames')
  },
  beforeDestroy () {
    this.unSubscribeGameLobbyChannel()
  },
  beforeRouteLeave (to, from, next) {
    next()
    this.unSubscribeGameLobbyChannel()
  }
}
</script>
