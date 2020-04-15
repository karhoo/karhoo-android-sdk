@Library(value="service-builder-shared-library@master", changelog=false) _

simpleCICD {
    failureNotificationChannel = "release-notifications"
    successNotificationChannel = "release-notifications"
    containerImages = [:]
    containerImages["builder"] = [name:"karhoo-android",tag:"0.1.0"]
    makeTargets = []
}
