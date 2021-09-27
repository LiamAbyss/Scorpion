/* eslint-disable @typescript-eslint/no-var-requires */
const webpackConfig = require('./webpack.config')

webpackConfig.output.publicPath = '/Scorpion/dist/'

module.exports = webpackConfig
