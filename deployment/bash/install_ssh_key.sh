#!/bin/bash
mkdir -p ansible/ssh_keys
ssh-keygen -q -t rsa -N '' -f ansible/ssh_keys/id_rsa_ansible <<<n >/dev/null 2>&1
for host in "$@"; do
  echo $host
  ssh-copy-id -f -i ansible/ssh_keys/id_rsa_ansible.pub -o StrictHostKeyChecking=no "$host"
done
