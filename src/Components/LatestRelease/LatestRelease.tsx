import '@fortawesome/fontawesome-free/scss/fontawesome.scss'
import '@fortawesome/fontawesome-free/scss/solid.scss'
import '@fortawesome/fontawesome-free/scss/regular.scss'
import './LatestRelease.scss'

import React, { Component } from 'react'
import { GithubRelease } from '../../Github'
import moment from 'moment'

// ---- Properties -----------------------------------------------------------------------
export interface LatestReleaseProps {
	release: GithubRelease
}

// ---- Component ------------------------------------------------------------------------
export class LatestRelease extends Component<LatestReleaseProps> {
	constructor(props: LatestReleaseProps) {
		super(props)
	}

	public render() {
		const { release } = this.props
		const dayDiff = moment().diff(moment(release.published_at), 'days') + 1

		return (
			<div className="parent">
				<div className="download">
					<div className="title">
						{release.prerelease ? (
							<i className="fas fa-exclamation-triangle" title="This is a pre-release"></i>
						) : (
							<i className="fas fa-star-half-alt"></i>
						)}{' '}
						Download Latest
						<br />
						<small>{release.tag_name}</small>
						<br />
						<small>{dayDiff === 1 ? 'Today' : `${dayDiff} days ago`}</small>
					</div>

					<div className="links">
						<div className="link">
							<a href={release.assets[0].browser_download_url}>
								<i className="fa fa-download"></i>
								<span className="link-label">apk</span>
							</a>
						</div>

						<div className="link">
							<a href={release.html_url}>
								<i className="fas fa-file-alt"></i>
								<span className="link-label">Changelog</span>
							</a>
						</div>
					</div>
				</div>
			</div>
		)
	}
}
