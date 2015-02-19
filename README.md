ConcordChurch
===========

Support off-line web-app, version control for web resource 

ㅇ Function

	- Caching resource from web site
		(http://52.0.156.206:9000)
	- Version control for each resource
		(http://52.0.156.206:9000/resources.json)

ㅇ Flow

	- Loading the installed web resource (for initial page):
		/ConcordChurch/assets/www
		- After that, launching caching web resources from web site.:
			Environment.getExternalStorageDirectory() + "/churchapp"
			
	- As of the next launching, read from cached web resource
		- Checking version with background job.: 
			http://52.0.156.206:3001/resources.json
		- If version changed file exist, cache the file: 
			Environment.getExternalStorageDirectory() + "/churchapp"




