<template>
  <div class="container public">
    <div class="row justify-content-center">
      <div class="form">
        <Logo />
        <form @submit.prevent="submitForm">
          <div v-show="errorMessage" class="alert alert-danger failed">
            {{ errorMessage }}
          </div>
          <div class="form-group">
            <label for="username">{{
              $t("loginPage.form.username.label")
            }}</label>
            <input
              type="text"
              class="form-control"
              id="username"
              v-model="form.username"
            />
            <div class="field-error" v-if="$v.form.username.$dirty">
              <div class="error" v-if="!$v.form.username.required">
                {{ $t("loginPage.form.username.required") }}
              </div>
            </div>
          </div>
          <div class="form-group">
            <label for="password">{{
              $t("loginPage.form.password.label")
            }}</label>
            <input
              type="password"
              class="form-control"
              id="password"
              v-model="form.password"
            />
            <div class="field-error" v-if="$v.form.password.$dirty">
              <div class="error" v-if="!$v.form.password.required">
                {{ $t("loginPage.form.password.required") }}
              </div>
            </div>
          </div>
          <br />
          <button type="submit" class="btn btn-primary w-100">
            {{ $t("loginPage.form.submit") }}
          </button>
          <div class="links">
            <p class="sign-up text-muted">
              {{ $t("loginPage.form.noAccountYet") }}
              <router-link to="register" class="link-sign-up">{{
                $t("loginPage.form.signUpHere")
              }}</router-link>
            </p>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { required } from 'vuelidate/lib/validators'
import authenticationService from '@/services/authentication'
import Logo from '@/components/Logo.vue'

export default {
  name: 'Login',
  data: function () {
    return {
      form: {
        username: '',
        password: ''
      },
      errorMessage: ''
    }
  },
  components: {
    Logo
  },
  validations: {
    form: {
      username: {
        required
      },
      password: {
        required
      }
    }
  },
  methods: {
    submitForm () {
      this.$v.$touch()
      if (this.$v.$invalid) {
        return
      }

      authenticationService
        .authenticate(this.form)
        .then(() => {
          this.$router.push({ name: 'home' })
          this.$bus.$emit('authenticated')
        })
        .catch((error) => {
          this.errorMessage = error.message
        })
    }
  }
}
</script>

<style lang="scss" scoped>
.public.container {
  max-width: 900px;
}
input.form-control:focus,
textarea.form-control:focus {
  border: 1px solid #377EF6 !important;
}
.public {
  .form {
    margin-top: 50px;
    width: 320px;

    .form-group {
      label {
        font-weight: bold;
        color: #555;
      }

      .error {
        line-height: 1;
        margin-top: 5px;
      }
    }
  }
}
.links {
  margin: 30px 0 50px 0;
  text-align: center;
}
</style>
