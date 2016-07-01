require 'spec_helper'
describe 'jenkinshelper' do

  context 'with defaults for all parameters' do
    it { should contain_class('jenkinshelper') }
  end
end
