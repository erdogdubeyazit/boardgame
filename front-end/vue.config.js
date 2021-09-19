module.exports = {
  devServer: {
    port: 3000,
    proxy: {
      '/api/*': {
        target: 'http://localhost:8080'
      },
      '/wsc/*': {
        target: 'http://localhost:8080'
      }
    }
  },
  configureWebpack: {
    entry: {
      app: './src/main.js',
      style: [
        'bootstrap/dist/css/bootstrap.min.css',
        'noty/lib/noty.css',
        'noty/lib/themes/relax.css'
      ]
    }
  }
}
