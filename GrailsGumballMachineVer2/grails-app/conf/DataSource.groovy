dataSource 
{
pooled = true
jmxExport = true
//driverClassName = "org.h2.Driver"
driverClassName = "com.mysql.jdbc.Driver"
dialect="org.hibernate.dialect.MySQL5InnoDBDialect"
//username = "root"
//password = ""
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
//    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory' // Hibernate 3
    cache.region.factory_class = 'org.hibernate.cache.ehcache.EhCacheRegionFactory' // Hibernate 4
    singleSession = true // configure OSIV singleSession mode
    flush.mode = 'manual' // OSIV session flush mode outside of transactional context
}

// environment specific settings
environments {
	
	development {
		dataSource {
			dbCreate = "create-drop" // one of 'create', 'create-drop', 'update', 'validate', ''
			url = "jdbc:mysql://localhost:3306/rachitraut"
			driverClassName = "com.mysql.jdbc.Driver"
			username = "root"
			password = "1234"
		}
	}
	test {
		dataSource {
			dbCreate = "update"  // 'create', 'create-drop','update'
			url = "jdbc:mysql://localhost:3306/cmpe281"
			driverClassName = "com.mysql.jdbc.Driver"
			username = "root"
			password = ""
		}
	}
	production {
		dataSource {
			dbCreate = "update"  // 'create', 'create-drop','update'
			url = "mysql://mysql://bff307742d0c76:cb134b54@us-cdbr-iron-east-01.cleardb.net:3306/ad_58c32dcd71b256f?reconnect=true"
			driverClassName = "com.mysql.jdbc.Driver"
			username = "bff307742d0c76"
			password = "cb134b54"
		}

	}
		
}
