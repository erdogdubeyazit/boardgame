<template>
  <form @submit.prevent="saveGame">
    <div
      class="modal"
      tabindex="-1"
      role="dialog"
      backdrop="static"
      id="createGameModal"
    >
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ $t("homePage.createModal.title") }}</h5>
            <button
              type="button"
              class="close"
              @click="close"
              aria-label="Close"
            >
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
          <div class="modal-body">
            <div v-show="errorMessage" class="alert alert-danger failed">
              {{ errorMessage }}
            </div>
            <div class="form-group">
              <input
                type="text"
                class="form-control"
                id="pitCountInput"
                v-model="pitCount"
                :placeholder="$t('homePage.createModal.pitCount.label')"
                maxlength="128"
              />
              <div class="field-error" v-if="$v.pitCount.$dirty">
              <div class="error" v-if="!$v.pitCount.required">
                {{ $t("homePage.createModal.pitCount.required") }}
              </div>
              <div class="error" v-if="!$v.pitCount.minValue">
                {{
                  $t("homePage.createModal.pitCount.minValue", {
                    minValue: $v.pitCount.$params.minValue.min,
                  })
                }}
              </div>
            </div>
            </div>
            <div class="form-group">
              <input
                type="text"
                class="form-control"
                id="itemsPerPitInput"
                v-model="itemsPerPit"
                :placeholder="$t('homePage.createModal.itemsPerPit.label')"
                maxlength="2"
              />
              <div class="field-error" v-if="$v.itemsPerPit.$dirty">
              <div class="error" v-if="!$v.itemsPerPit.required">
                {{ $t("homePage.createModal.itemsPerPit.required") }}
              </div>
              <div class="error" v-if="!$v.itemsPerPit.minValue">
                {{
                  $t("homePage.createModal.itemsPerPit.minValue", {
                    minValue: $v.itemsPerPit.$params.minValue.min,
                  })
                }}
              </div>
            </div>
            </div>
          </div>
          <div class="modal-footer">
            <button type="submit" class="btn btn-primary">{{ $t("homePage.createModal.save") }}</button>
            <button
              type="button"
              class="btn btn-default btn-cancel"
              @click="close"
            >
              {{ $t("homePage.createModal.cancel") }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </form>
</template>
<script>
import $ from 'jquery'
import { required, minValue } from 'vuelidate/lib/validators'
import gameService from '@/services/game'

export default {
  name: 'CreateGameModal',
  data () {
    return {
      pitCount: '',
      itemsPerPit: '',
      errorMessage: ''
    }
  },
  validations: {
    pitCount: {
      required,
      minValue: minValue(1)
    },
    itemsPerPit: {
      required,
      minValue: minValue(1)
    }
  },
  methods: {
    saveGame () {
      this.$v.$touch()
      if (this.$v.$invalid) {
        return
      }
      gameService
        .createGame(this.pitCount, this.itemsPerPit)
        .then((data) => {
          this.$emit('gameSaved', data)
          this.close()
        })
        .catch((error) => {
          this.errorMessage = error.message
        })
    },
    close () {
      this.$v.$reset()
      this.pitCount = ''
      this.itemsPerPit = ''
      this.errorMessage = ''
      $('#createGameModal').modal('hide')
    }
  },
  mounted () {
    $('#createGameModal').on('shown.bs.modal', () => {
      $('#pitCountInput').trigger('focus')
    })
  }
}
</script>
