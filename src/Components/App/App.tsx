import './App.scss'

import axios from 'axios'
import React, { Component } from 'react'
import banner from './Banner.png'
import { GithubRelease } from '../../Github'
import { LatestRelease } from '../LatestRelease/LatestRelease'
import { ReleaseList } from '../ReleaseList/ReleaseList'

// ---- Properties -----------------------------------------------------------------------
// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface AppProps {}

// ---- State ----------------------------------------------------------------------------
export interface AppState {
	latest: GithubRelease | null
	releases: GithubRelease[]
}

// ---- Component ------------------------------------------------------------------------
export class App extends Component<AppProps, AppState> {
	constructor(props: AppProps) {
		super(props)

		this.state = {
			latest: null,
			releases: []
		}
	}

	async componentDidMount(): Promise<void> {
		const res = await axios.get('https://api.github.com/repos/LiamAbyss/Scorpion/releases')

		if (res.data.length > 0) {
			const latest = res.data[0]
			const all = res.data

			this.setState({ latest, releases: all })
		}
	}

	public render() {
		const { releases: all, latest } = this.state

		return (
			<div className="app">
				<img className="banner" src={banner} />
				{latest != null && <LatestRelease release={latest} />}
				<ReleaseList releases={all} />
			</div>
		)
	}
}
