# == Defines: jenkinshelper::plugins::set_simpletheme
#
# Set the LDAP configuration
#
define jenkinshelper::plugins::set_simpletheme(
  $cssURL = undef,
  $jsURL = undef,
  $unless = undef,
) {
  include ::jenkinshelper
  include ::jenkinshelper::cli_helper

  Class['jenkinshelper::cli_helper']->
    jenkinshelper::plugins::set_simpletheme[$title]->
      Anchor['jenkinshelper::end']

  $run = join(
    delete_undef_values(
      flatten([
        $::jenkinshelper::cli_helper::helper_cmd,
        'set_swi_style',
        $cssURL,
        $jsURL,
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
