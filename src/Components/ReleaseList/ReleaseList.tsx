import './ReleaseList.scss'

import moment from 'moment'
import React, { Component } from 'react'
import { GithubRelease } from '../../Github'

// ---- Properties -----------------------------------------------------------------------
export interface ReleaseListProps {
	releases: GithubRelease[]
}

// ---- Component ------------------------------------------------------------------------
export class ReleaseList extends Component<ReleaseListProps> {
	constructor(props: ReleaseListProps) {
		super(props)

		this.state = {}
	}

	public render() {
		// Add dayDiff field
		const releases = this.props.releases.map((release: GithubRelease) => ({
			...{ dayDiff: moment().diff(moment(release.published_at), 'days') + 1 },
			...release
		}))

		return (
			<table className="release-list">
				<thead>
					<tr>
						<th>Version</th>
						<th>Time</th>
						<th>Download</th>
						<th>Changelog</th>
					</tr>
				</thead>
				<tbody>
					{releases.map((release, idx) => (
						<tr key={idx}>
							<td className="version">
								{release.prerelease && (
									<i className="fas fa-exclamation-triangle" title="This is a pre-release"></i>
								)}{' '}
								{idx == 0 && <i className="fas fa-star-half-alt" title="Latest"></i>} {release.name}
							</td>
							<td className="time">{release.dayDiff === 1 ? 'Today' : `${release.dayDiff} days ago`}</td>
							<td className="download">
								<a href={release.assets[0].browser_download_url}>
									<i className="fa fa-download"></i>
									<span className="link-label">apk</span>
								</a>
							</td>
							<td className="changelog">
								<a href={release.html_url}>
									<i className="fas fa-file-alt"></i>
									<span className="link-label">Changelog</span>
								</a>
							</td>
						</tr>
					))}
				</tbody>
			</table>
		)
	}
}
