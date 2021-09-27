import './App.scss'

import axios from 'axios'
import React, { Component } from 'react'
import banner from './Banner.png'
import { GithubRelease } from '../../Github'
import { LatestRelease } from '../LatestRelease/LatestRelease'

// ---- Properties -----------------------------------------------------------------------
// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface AppProps {}

// ---- State ----------------------------------------------------------------------------
export interface AppState {
	latest: GithubRelease | null
	all: GithubRelease[]
}

// ---- Component ------------------------------------------------------------------------
export class App extends Component<AppProps, AppState> {
	constructor(props: AppProps) {
		super(props)

		this.state = {
			latest: null,
			all: []
		}
	}

	async componentDidMount(): Promise<void> {
		const res = await axios.get('https://api.github.com/repos/LiamAbyss/Scorpion/releases')

		if (res.data.length > 0) {
			const latest = res.data[0]
			const all = res.data

			this.setState({ latest, all })
		}
	}

	public render() {
		const { all, latest } = this.state

		return (
			<div className="app">
				<img className="banner" src={banner} />
				{latest != null && <LatestRelease release={latest} />}
			</div>
		)
	}
}
