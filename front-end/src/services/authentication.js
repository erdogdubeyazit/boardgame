import axios from 'axios'
import errorParser from '@/utils/error-parser'

export default {
  authenticate (payload) {
    return new Promise((resolve, reject) => {
      axios.post('/authentications', payload).then(({ data }) => {
        resolve(data)
      }).catch((error) => {
        reject(errorParser.parse(error))
      })
    })
  }
}
