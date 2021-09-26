/* eslint-disable @typescript-eslint/no-var-requires */
const path = require('path')
const CssMinimizerPlugin = require('css-minimizer-webpack-plugin')
const ESLintPlugin = require('eslint-webpack-plugin')
const MiniCssExtractPlugin = require('mini-css-extract-plugin')
const StylelintPlugin = require('stylelint-webpack-plugin')

module.exports = {
	mode: 'development',

	entry: './src/index.tsx',

	context: path.resolve(__dirname),

	resolve: {
		extensions: ['.ts', '.tsx', '.js', '.jsx', '.json']
	},

	output: {
		path: path.resolve(__dirname, 'public', 'dist'),
		filename: 'js/app.js',
		publicPath: '/dist/'
	},

	module: {
		rules: [
			{ test: /\.tsx?$/, loader: 'ts-loader' },
			{
				test: /\.css$/i,
				use: [MiniCssExtractPlugin.loader, 'css-loader']
			},
			{
				test: /\.s[ac]ss$/i,
				use: [
					MiniCssExtractPlugin.loader,
					'css-loader',
					{
						loader: 'postcss-loader',
						options: {
							postcssOptions: {
								plugins: ['autoprefixer']
							}
						}
					},
					'sass-loader'
				]
			},
			{
				test: /\.(woff2?|eot|ttf|otf)(\?.*)?$/i,
				type: 'asset/resource',
				generator: {
					filename: 'fonts/[hash][ext]'
				}
			},
			{
				test: /\.(png|jpg|gif|svg)$/i,
				type: 'asset/resource',
				generator: {
					filename: 'img/[hash][ext]'
				}
			}
		]
	},

	plugins: [
		new ESLintPlugin({
			fix: true,
			emitWarning: true
		}),
		new StylelintPlugin({
			fix: true,
			emitWarning: true
		}),
		new MiniCssExtractPlugin({
			filename: 'css/app.css'
		})
	],

	devtool: 'source-map',

	optimization: {
		minimizer: ['...', new CssMinimizerPlugin()]
	},

	devServer: {
		static: path.join(__dirname, 'public'),
		compress: true,
		hot: true,
		port: 9000
	}
}
