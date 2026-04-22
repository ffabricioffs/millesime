#!/bin/sh
if [ -f .env ]; then
  while IFS='=' read -r key value; do
    # Ignorar linhas vazias ou comentários
    case "$key" in
      ''|\#*) continue ;;
    esac
    export "$key"="$value"
    echo "Exportado: $key"
  done < .env
else
  echo "Arquivo .env não encontrado!"
  exit 1
fi