#!/bin/bash
set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
export JAVA_HOME="$DIR/.tools/jdk-17.0.20.1+1/Contents/Home"
export PATH="$JAVA_HOME/bin:$DIR/.tools/apache-maven-3.9.6/bin:$PATH"

echo "========================================="
echo " E-Healthcare Clinic Manager - Startup"
echo "========================================="
echo "Java:  $("$JAVA_HOME/bin/java" -version 2>&1 | head -n 1)"
echo "Maven: $("$DIR/.tools/apache-maven-3.9.6/bin/mvn" -version 2>&1 | head -n 1)"

# Check MySQL status only if configured to use MySQL
if grep -q "com.mysql.cj.jdbc.Driver" "$DIR/src/main/resources/db.properties" 2>/dev/null && ! grep -q "^#.*com.mysql" "$DIR/src/main/resources/db.properties"; then
  if ! nc -z 127.0.0.1 3306 2>/dev/null; then
    echo ""
    echo "⚠️  WARNING: MySQL is not running on localhost:3306!"
    echo "Please start MySQL before accessing the application."
    echo "If using Homebrew: brew services start mysql"
    echo ""
  fi
else
  echo "Database: Using embedded H2 database (standalone mode, no external server needed)"
fi

# Build project
echo "Packaging application..."
"$DIR/.tools/apache-maven-3.9.6/bin/mvn" package -DskipTests -o

TOMCAT_DIR="$DIR/.tools/apache-tomcat-10.1.24"

# Deploy
echo "Deploying WAR to Tomcat..."
rm -rf "$TOMCAT_DIR/webapps/ehealthcare-clinic-manager"
cp "$DIR/target/ehealthcare-clinic-manager.war" "$TOMCAT_DIR/webapps/"

# Stop existing tomcat if running
pkill -f "apache-tomcat-10.1.24" 2>/dev/null || true
sleep 1

echo ""
echo "Starting Tomcat server on port 8080..."
echo "Application URL: http://localhost:8080/ehealthcare-clinic-manager/"
echo "Press Ctrl+C to stop."
echo "========================================="
"$TOMCAT_DIR/bin/catalina.sh" run
