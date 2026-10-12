#!/bin/bash
set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "========================================="
echo " Initializing clinic_db in MySQL"
echo "========================================="

echo "Enter your MySQL root password (press Enter if password is 'root' or blank):"
read -s DB_PASS

if [ -z "$DB_PASS" ]; then
  # Try connecting without password, then with root
  if mysql -u root -e "SELECT 1;" >/dev/null 2>&1; then
    DB_PASS=""
  else
    DB_PASS="root"
  fi
fi

if [ -n "$DB_PASS" ]; then
  MYSQL_CMD="mysql -u root -p$DB_PASS"
else
  MYSQL_CMD="mysql -u root"
fi

$MYSQL_CMD -e "CREATE DATABASE IF NOT EXISTS clinic_db;"
$MYSQL_CMD clinic_db < "$DIR/db/schema.sql"

echo "✅ Schema loaded into 'clinic_db' successfully!"
echo "You can now run ./run.sh to start the server."
