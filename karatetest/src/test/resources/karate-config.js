function() {
  var env = karate.env;
  if (!env) {
    env = 'test';
  }

  var config = {
    connectTimeout: karate.properties['connectTimeout'],
    readTimeout: karate.properties['readTimeout'],
    retryCount: karate.properties['retryCount'],
    retryInterval: karate.properties['retryInterval']
  };

  if (env == 'test') {
    config.db1 = {
      username: karate.properties['test.username'],
      password: karate.properties['test.password'],
      url: karate.properties['test.url'],
      driverClassName: karate.properties['test.driverClassName']
    };

    config.db2 = {
      username: karate.properties['test.username'],
      password: karate.properties['test.password'],
      nbsdburl: karate.properties['test.nbsdburl'],
      driverClassName: karate.properties['test.driverClassName']
    };
    config.apiurl = karate.properties['test.apiurl'];
    config.bootstrapServers = karate.properties['test.bootstrapServers'];
    config.groupId = karate.properties['test.groupId'];
    config.apiusername = karate.properties['test.apiusername'];
    config.apipassword = karate.properties['test.apipassword'];
    config.wrongapiurl = karate.properties['test.wrongapiurl'];
    config.nbsurl = karate.properties['test.nbsurl'];


  } else if (env == 'dev') {
    config.db1 = {
      username: karate.properties['dev.username'],
      password: karate.properties['dev.password'],
      url: karate.properties['dev.url'],
      driverClassName: karate.properties['dev.driverClassName']
    };

    config.db2 = {
      username: karate.properties['dev.nbsinterfaceusername'],
      password: karate.properties['dev.nbsinterfacepwd'],
      url: karate.properties['dev.nbsdburl'],
      driverClassName: karate.properties['dev.nbsdriverclsName']

    };
    config.apiurl = karate.properties['dev.apiurl'];
    config.bootstrapServers = karate.properties['dev.bootstrapServers'];
    config.groupId = karate.properties['dev.groupId'];
    config.apiusername = karate.properties['dev.apiusername'];
    config.apipassword = karate.properties['dev.apipassword'];
    config.wrongapiurl = karate.properties['dev.wrongapiurl'];
    config.nbsurl = karate.properties['dev.nbsurl'];
  } else {

    return;
  }

  return config;
}
