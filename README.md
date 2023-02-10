brew install redis
Starting and stopping Redis using launchd
As an alternative to running Redis in the foreground, you can also use launchd to start the process in the background:

brew services start redis
This launches Redis and restarts it at login. You can check the status of a launchd managed Redis by running the following:

brew services info redis
If the service is running, you'll see output like the following:
URL:
https://redis.io/docs/getting-started/installation/install-redis-on-mac-os/