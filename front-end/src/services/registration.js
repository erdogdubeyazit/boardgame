import axios from 'axios'
import errorParser from '@/utils/error-parser'

export default {

  register (payload) {
    return new Promise((resolve, reject) => {
      axios.post('/registrations', payload).then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        reject(errorParser.parse(error))
      })
    })
  }
}
