#!/bin/bash
# =============================================================================
# Deployment Script for Spring Petclinic
# Target: Wildfly 20+
# =============================================================================

# Configuration
WILDFLY_HOME="/opt/wildfly"
DEPLOYMENTS_DIR="$WILDFLY_HOME/standalone/deployments"
WAR_SOURCE="target/petclinic.war"
BACKUP_DIR="/opt/backups/petclinic"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)

echo "Starting deployment process..."

# 1. Validate environment
if [ ! -f "$WAR_SOURCE" ]; then
    echo "Error: Source WAR file not found at $WAR_SOURCE"
    exit 1
fi

if [ ! -d "$DEPLOYMENTS_DIR" ]; then
    echo "Error: Wildfly deployments directory not found at $DEPLOYMENTS_DIR"
    exit 1
fi

# 2. Create backup
echo "Creating backup..."
mkdir -p "$BACKUP_DIR"
if [ -f "$DEPLOYMENTS_DIR/petclinic.war" ]; then
    cp "$DEPLOYMENTS_DIR/petclinic.war" "$BACKUP_DIR/petclinic.war.$TIMESTAMP"
    echo "Backup created at $BACKUP_DIR/petclinic.war.$TIMESTAMP"
else
    echo "No existing deployment to backup."
fi

# 3. Deploy new artifact
echo "Deploying new artifact..."
# Remove marker files to ensure clean deployment
rm -f "$DEPLOYMENTS_DIR/petclinic.war.deployed"
rm -f "$DEPLOYMENTS_DIR/petclinic.war.failed"

# Copy WAR file
cp "$WAR_SOURCE" "$DEPLOYMENTS_DIR/petclinic.war"

echo "Artifact copied. Waiting for Wildfly to pick up changes..."
sleep 5

# 4. Verify deployment (Basic check)
# In a real environment, you might check distinct marker files or query the management API
if [ -f "$DEPLOYMENTS_DIR/petclinic.war.failed" ]; then
    echo "Deployment FAILED. Wildfly creation of .failed marker detected."
    # Rollback logic could go here
    exit 1
elif [ -f "$DEPLOYMENTS_DIR/petclinic.war.deployed" ]; then
    echo "Deployment SUCCESS. Wildfly creation of .deployed marker detected."
else
    echo "Deployment status unknown (no marker file yet). Please check server logs."
fi

echo "Deployment script completed."
exit 0
