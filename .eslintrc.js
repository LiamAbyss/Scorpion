module.exports = {
	root: true,
	parser: '@typescript-eslint/parser',

	env: {
		browser: true,
		node: true
	},

	parserOptions: {
		ecmaVersion: 11,
		ecmaFeatures: {
			jsx: true
		}
	},

	plugins: ['@typescript-eslint', 'prettier'],

	extends: ['eslint:recommended', 'prettier', 'plugin:@typescript-eslint/recommended'],

	rules: {
		'prettier/prettier': 'error',
		'sort-imports': 'off',
		'import/order': 'off',
		'@typescript-eslint/no-explicit-any': 'off',
		'@typescript-eslint/explicit-module-boundary-types': 'off'
	},

	settings: {
		react: {
			version: 'detect'
		}
	}
}
