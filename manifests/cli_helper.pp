# == Class: jenkinshelper::cli_helper
class jenkinshelper::cli_helper {
  Class['jenkinshelper'] ->
    Class['jenkinshelper::cli_helper']
  $helper_cmd = join(
    delete_undef_values([
      '/usr/bin/java',
      "-jar ${::jenkinshelper::jar}",
      "-s http://127.0.0.1:${::jenkinshelper::port}",
      "groovy ${::jenkinshelper::libdir}/swi.groovy",
    ]),
    ' '
  )
}
