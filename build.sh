#! /usr/bin/env bash
set -e

basepath=$(cd `dirname $0`; pwd)

print_info() {
    echo -e "[\033[1;34mINFO\033[0m] $1"
}

print_err() {
    echo -e "[\033[1;31mError\033[0m] $1"
}

start-phoenix-studio() {
    print_info "start phoenix-studio frontend server"
    cd $basepath/phoenix-studio
    npm run serve
    cd -
}

build-phoenix-studio() {
    print_info "start build phoenix-studio"
    cd $basepath/phoenix-studio
    npm install
    npm run build
    cd -
}

build-phoenix-studio-backend() {
    print_info "start build phoenix-studio backend"
    cd $basepath/backend
    npm install
    npm run build
    cd -
}

usage() {
    echo "build [options]"
    echo "Options:"
    echo "-s    start phoenix-studio server"
    echo "-b    build-ui phoenix-studio"
    echo "help  show usage information"
}

if [ $# -eq 0 ]; then
    usage
    exit 0
fi

while [[ $# -gt 0 ]]; do
    opt=$1
    shift
    case $opt in
    -s)
        COMMANDS+=("start-phoenix-studio")
        ;;
    -b)
        COMMANDS+=("build-phoenix-studio")
        ;;
    help | *)
        usage
        exit 0
        ;;
    esac
done

for i in "${COMMANDS[@]}"
do
    $i
done
