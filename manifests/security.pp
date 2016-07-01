# == Defines: jenkinshelper::security::set_ldap
#
# Set the LDAP configuration
#
define jenkinshelper::security::set_ldap(
  $ldap_url        = undef,
  $basedn          = undef,
  $strategy_type   = undef,
  $permission_list = undef,
  $unless          = undef,
) {
  include ::jenkinshelper
  include ::jenkinshelper::cli_helper

  Class['jenkinshelper::cli_helper']->
    jenkinshelper::security::set_ldap[$title]->
      Anchor['jenkinshelper::end']

  if $ldap_url {
    if $ldap_url == 'disabled' {
      $basedn          = undef
      $strategy_type   = undef
      $permission_list = undef
    }

    if $strategy_type == 'matrix' {
      $permission_list = join($permission_list::,
    } else {
      $permission_list = undef
    }

    if is_string($permission_list) and $permission_list == 'default' {
      $permission_list = [
        'com.cloudbees.plugins.credentials.CredentialsProvider.VIEW:ROLE_JENKINSADMIN',
        'hudson.model.Computer.BUILD:ROLE_JENKINSADMIN',
        'hudson.model.Computer.CONNECT:ROLE_JENKINSADMIN',
        'hudson.model.Computer.CREATE:ROLE_JENKINSADMIN',
        'hudson.model.Computer.DELETE:ROLE_JENKINSADMIN',
        'hudson.model.Computer.DISCONNECT:ROLE_JENKINSADMIN',
        'jenkins.model.Jenkins.ADMINISTER:ROLE_JENKINSADMIN',
        'hudson.PluginManager.CONFIGURE_UPDATECENTER:ROLE_JENKINSADMIN',
        'jenkins.model.Jenkins.READ:ROLE_JENKINSADMIN',
        'jenkins.model.Jenkins.READ:ROLE_JENKINSUSER',
        'jenkins.model.Jenkins.READ:anonymous',
        'jenkins.model.Jenkins.RUN_SCRIPTS:ROLE_JENKINSADMIN',
        'hudson.PluginManager.UPLOAD_PLUGINS:ROLE_JENKINSADMIN',
        'hudson.model.Item.BUILD:ROLE_JENKINSADMIN',
        'hudson.model.Item.CANCEL:ROLE_JENKINSADMIN',
        'hudson.model.Item.CONFIGURE:ROLE_JENKINSADMIN',
        'hudson.model.Item.CREATE:ROLE_JENKINSADMIN',
        'hudson.model.Item.DELETE:ROLE_JENKINSADMIN',
        'hudson.model.Item.DISCOVER:ROLE_JENKINSADMIN',
        'hudson.model.Item.DISCOVER:ROLE_JENKINSUSER',
        'hudson.model.Item.EXTENDED_READ:ROLE_JENKINSADMIN',
        'hudson.model.Item.EXTENDED_READ:ROLE_JENKINSUSER',
        'hudson.model.Item.READ:ROLE_JENKINSADMIN',
        'hudson.model.Item.READ:ROLE_JENKINSUSER',
        'hudson.model.Item.READ:anonymous',
        'hudson.model.Item.WORKSPACE:ROLE_JENKINSADMIN',
        'hudson.model.Item.WORKSPACE:ROLE_JENKINSUSER',
        'hudson.model.Item.WORKSPACE:anonymous',
        'hudson.model.Run.DELETE:ROLE_JENKINSADMIN',
        'hudson.model.Run.UPDATE:ROLE_JENKINSADMIN',
        'hudson.model.Run.UPDATE:ROLE_JENKINSUSER',
        'hudson.model.View.CONFIGURE:ROLE_JENKINSADMIN',
        'hudson.model.View.CREATE:ROLE_JENKINSADMIN',
        'hudson.model.View.DELETE:ROLE_JENKINSADMIN',
        'hudson.model.View.READ:ROLE_JENKINSADMIN',
        'hudson.model.View.READ:ROLE_JENKINSUSER',
        'hudson.model.View.READ:anonymous',
      ]
    }
    $run = join(
      delete_undef_values(
        flatten([
          $::jenkinshelper::cli_helper::helper_cmd,
          'set_ldap',
          $ldap_url,
          $basedn,
          $strategy_type,
          $permission_list
        ])
      ),
      ' '
    )

    exec { $title:
      provider  => 'shell',
      command   => $run,
      unless    => $unless,
      tries     => $::jenkinshelper::cli_tries,
      try_sleep => $::jenkinshelper::cli_try_sleep,
    }
  }


}
