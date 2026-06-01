#!/bin/sh

envsubst < /tmp/httpd.conf.template > /usr/local/apache2/conf/httpd.conf

httpd-foreground