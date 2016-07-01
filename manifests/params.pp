# == Class: jenkinshelper::params
class jenkinshelper::params {
  $user          = 'jenkins'
  $group         = 'jenkins'
  $jar           = '/usr/share/jenkins/jenkins-cli.jar'
  $port          = 8080
  $cli_tries     = 10
  $cli_try_sleep = 10

  case $::osfamily {
    'Debian': {
      $libdir           = '/usr/share/jenkins'
      $package_provider = 'dpkg'
      $service_provider = undef
    }
    'RedHat': {
      $libdir           = '/usr/lib/jenkins'
      $package_provider = 'rpm'
      case $::operatingsystem {
        'Fedora': {
          if versioncmp($::operatingsystemrelease, '19') >= 0 or $::operatingsystemrelease == 'Rawhide' {
            $service_provider = 'redhat'
          }
        }
        /^(RedHat|CentOS|Scientific|OracleLinux)$/: {
          if versioncmp($::operatingsystemmajrelease, '7') >= 0 {
            $service_provider = 'redhat'
          }
        }
        default: {
          $service_provider = undef
        }
      }
    }
    default: {
      $libdir           = '/usr/lib/jenkins'
      $package_provider = undef
      $service_provider = undef
    }
  }
}
