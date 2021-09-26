// ---- Interfaces -----------------------------------------------------------------------
export interface GithubUser {
	avatar_url: string
	events_url: string
	followers_url: string
	following_url: string
	gists_url: string
	gravatar_id: string
	html_url: string
	id: number
	login: string
	node_id: string
	organizations_url: string
	received_events_url: string
	repos_url: string
	site_admin: boolean
	starred_url: string
	subscriptions_url: string
	type: string
}

export interface GithubReleaseAsset {
	browser_download_url: string
	content_type: string
	created_at: string
	download_count: number
	id: number
	label: string | null
	name: string
	node_id: string
	size: number
	state: string
	updated_at: string
	uploader: GithubUser
	url: string
}

export interface GithubRelease {
	assets: GithubReleaseAsset[]
	assets_url: string
	author: GithubUser
	body: string
	created_at: string
	draft: boolean
	html_url: string
	id: number
	name: string
	node_id: string
	prerelease: boolean
	published_at: string
	tag_name: string
	tarball_url: string
	target_commitish: string
	upload_url: string
	url: string
}
