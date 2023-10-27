const { name } = require('./package')
const path = require('path')
const MonacoWebpackPlugin = require('monaco-editor-webpack-plugin')
const MonacoLocalesPlugin = require('monaco-editor-locales-plugin')
const AntdDayjsWebpackPlugin = require('antd-dayjs-webpack-plugin')
const SERVER_PORT = 8080
const modifyVars = require('./src/smart-ui-vue/modifyVars.ts')
// const BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin
const CompressionPlugin = require('compression-webpack-plugin')

module.exports = {
  devServer: {
    headers: {
      'Access-Control-Allow-Origin': '*',
    },
    port: SERVER_PORT,
    proxy: {
      '/api': {
        target: 'http://82.157.43.12:7091/',
        ws: true,
        changOrigin: true,
        pathRewrite: {
          '^/api': '/'
        }
      }
    },
  },
  configureWebpack: {
    resolve: {
      alias: {
        '@': path.resolve(__dirname, './src'),
      },
      extensions: ['.js', '.vue', '.json'],
    },
    output: {
      library: `${name}-[name]`,
      libraryTarget: 'umd', // 把微应用打包成 umd 库格式
      jsonpFunction: `webpackJsonp_${name}`,
    },
    plugins: [
      new AntdDayjsWebpackPlugin(),
      // new BundleAnalyzerPlugin(),
      new CompressionPlugin()
    ],
  },
  chainWebpack: config => {
    config.module
      .rule('svg')
      .exclude.add(path.resolve('src/assets/icons'))
      .add(path.resolve('src/smart-ui-vue/assets/icons'))
      .end()

    config.module
      .rule('icons')
      .test(/\.svg$/)
      .include.add(path.resolve('src/assets/icons'))
      .add(path.resolve('src/smart-ui-vue/assets/icons'))
      .end()
      .use('svg-sprite-loader')
      .loader('svg-sprite-loader')
      .options({
        symbolId: filePath => {
          const newPath = filePath.split(path.sep).join('/')
          const svgname = newPath.substring(newPath.indexOf('icons/')).replace('icons/', 'phoenix-studio/').replace('.svg', '')
          return svgname
        },
      })

    config.module
      .rule('scss').oneOfs.store
      .forEach(item => {
        item
          .use('sass-resources-loader')
          .loader('sass-resources-loader')
          .options({
            resources: [
              './src/smart-ui-vue/styles/mixins.scss'
            ]
          })
          .end()
      })

    config.plugin('monaco').use(new MonacoWebpackPlugin())
    config.plugin('monaco').use(new MonacoLocalesPlugin({
      // 设置支持的语言
      languages: ['es', 'zh-cn'],
      // 默认语言
      defaultLanguage: 'zh-cn',
      // 打印不匹配的文本
      logUnmatched: false,
      // 自定义文本翻译
      mapLanguages: { 'zh-cn': { 'Peek References': '查找引用', 'Go to Symbol...': '跳到变量位置', 'Command Palette': '命令面板' } },
    }),
    )
  },
  css: {
    loaderOptions: {
      less: {
        lessOptions: {
          modifyVars,
          javascriptEnabled: true,
        },
      },
    },
  },
}