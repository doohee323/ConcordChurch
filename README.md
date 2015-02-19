ConcordChurch
===========

Support off-line web-app, version control for web resource 

http://52.0.156.206:3001

ㅇ function
	- caching resource from web site
	- version control for each resource

ㅇ flow
	1) loading the installed web resource (for initial page)
			: /ConcordChurch/assets/www
		- after that, launching caching web resources from web site.
			: Environment.getExternalStorageDirectory() + "/churchapp"
	2) as of the next launching, read from cached web resource
		- checking version with background job.
		  : http://52.0.156.206:3001/resources.json
		- if version changed file exist, cache the file
			: Environment.getExternalStorageDirectory() + "/churchapp"




