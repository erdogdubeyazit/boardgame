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
              $t("registerPage.form.username.label")
            }}</label>
            <input
              type="text"
              class="form-control"
              id="username"
              v-model="form.username"
            />
            <div class="field-error" v-if="$v.form.username.$dirty">
              <div class="error" v-if="!$v.form.username.required">
                {{ $t("registerPage.form.username.required") }}
              </div>
              <div class="error" v-if="!$v.form.username.alphaNum">
                {{ $t("registerPage.form.username.alphaNum") }}
              </div>
              <div class="error" v-if="!$v.form.username.minLength">
                {{
                  $t("registerPage.form.username.minLength", {
                    minLength: $v.form.username.$params.minLength.min,
                  })
                }}
              </div>
              <div class="error" v-if="!$v.form.username.maxLength">
                {{
                  $t("registerPage.form.username.maxLength", {
                    maxLength: $v.form.username.$params.maxLength.max,
                  })
                }}
              </div>
            </div>
          </div>
          <div class="form-group">
            <label for="name">{{ $t("registerPage.form.name.label") }}</label>
            <input
              type="text"
              class="form-control"
              id="name"
              v-model="form.name"
            />
            <div class="field-error" v-if="$v.form.name.$dirty">
              <div class="error" v-if="!$v.form.name.required">
                {{ $t("registerPage.form.name.required") }}
              </div>
              <div class="error" v-if="!$v.form.name.alpha">
                {{ $t("registerPage.form.name.alpha") }}
              </div>
              <div class="error" v-if="!$v.form.name.minLength">
                {{
                  $t("registerPage.form.name.minLength", {
                    minLength: $v.form.name.$params.minLength.min,
                  })
                }}
              </div>
              <div class="error" v-if="!$v.form.name.maxLength">
                {{
                  $t("registerPage.form.name.maxLength", {
                    maxLength: $v.form.name.$params.maxLength.max,
                  })
                }}
              </div>
            </div>
          </div>
          <div class="form-group">
            <label for="lastName">{{
              $t("registerPage.form.surname.label")
            }}</label>
            <input
              type="text"
              class="form-control"
              id="surname"
              v-model="form.surname"
            />
            <div class="field-error" v-if="$v.form.surname.$dirty">
              <div class="error" v-if="!$v.form.surname.required">
                {{ $t("registerPage.form.surname.required") }}
              </div>
              <div class="error" v-if="!$v.form.surname.alpha">
                {{ $t("registerPage.form.surname.alpha") }}
              </div>
              <div class="error" v-if="!$v.form.surname.minLength">
                {{
                  $t("registerPage.form.surname.minLength", {
                    minLength: $v.form.surname.$params.minLength.min,
                  })
                }}
              </div>
              <div class="error" v-if="!$v.form.surname.maxLength">
                {{
                  $t("registerPage.form.surname.maxLength", {
                    maxLength: $v.form.surname.$params.maxLength.max,
                  })
                }}
              </div>
            </div>
          </div>
          <div class="form-group">
            <label for="password">{{
              $t("registerPage.form.password.label")
            }}</label>
            <input
              type="password"
              class="form-control"
              id="password"
              v-model="form.password"
            />
            <div class="field-error" v-if="$v.form.password.$dirty">
              <div class="error" v-if="!$v.form.password.required">
                {{ $t("registerPage.form.password.required") }}
              </div>
              <div class="error" v-if="!$v.form.password.minLength">
                {{
                  $t("registerPage.form.password.minLength", {
                    minLength: $v.form.password.$params.minLength.min,
                  })
                }}
              </div>
              <div class="error" v-if="!$v.form.password.maxLength">
                {{
                  $t("registerPage.form.password.maxLength", {
                    maxLength: $v.form.password.$params.maxLength.max,
                  })
                }}
              </div>
            </div>
          </div>
          <br />
          <button type="submit" class="form-control btn btn-primary w-100">
            {{ $t("registerPage.form.submit") }}
          </button>
          <p class="links text-center text-muted">
            {{ $t("registerPage.form.alreadyHaveAccount") }}
            <router-link to="login">{{
              $t("registerPage.form.signIn")
            }}</router-link>
          </p>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import {
  required,
  minLength,
  maxLength,
  alphaNum,
  alpha
} from 'vuelidate/lib/validators'
import registrationService from '@/services/registration'
import Logo from '@/components/Logo.vue'

export default {
  name: 'RegisterPage',
  data: function () {
    return {
      form: {
        username: '',
        name: '',
        surname: '',
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
        required,
        minLength: minLength(2),
        maxLength: maxLength(50),
        alphaNum
      },
      name: {
        required,
        minLength: minLength(1),
        maxLength: maxLength(50),
        alpha
      },
      surname: {
        required,
        minLength: minLength(1),
        maxLength: maxLength(50),
        alpha
      },
      password: {
        required,
        minLength: minLength(6),
        maxLength: maxLength(30)
      }
    }
  },
  methods: {
    submitForm () {
      this.$v.$touch()
      if (this.$v.$invalid) {
        return
      }

      registrationService
        .register(this.form)
        .then(() => {
          this.$router.push({ name: 'login' })
        })
        .catch((error) => {
          this.errorMessage = 'Failed to register user. ' + error.message
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
  border: 1px solid #377ef6 !important;
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
