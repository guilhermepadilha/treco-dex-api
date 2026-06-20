# Quickstart: Cloud-Agnostic Media Storage & MinIO

Este guia explica como configurar e utilizar o novo sistema de armazenamento de mídias agnóstico em nuvem localmente utilizando MinIO.

## Arquitetura

O sistema utiliza a interface `CloudStorageProvider` (Porta de Saída) e a implementação `S3StorageProvider` (Adapter). Para desenvolvimento local, evitamos usar salvar arquivos diretamente no disco rígido do SO (`FileSystemStorage`). Em vez disso, utilizamos o **MinIO**, um servidor de object-storage que é 100% compatível com a API S3 da AWS. Isso garante que o exato mesmo código que roda localmente rodará em produção, reduzindo bugs e mantendo a paridade entre os ambientes.

## Executando Localmente (MinIO)

1. **Suba os containers com o Docker Compose:**

   Na raiz do projeto, inicie os serviços do banco de dados e do MinIO executando:
   ```bash
   docker-compose -f docker/docker-compose.yml up -d
   ```

2. **O que acontece em background?**
   - O serviço `minio` iniciará o storage S3-compatible nas portas `9000` (API de integração) e `9001` (Console Web de administração).
   - O serviço efêmero `minio-createbucket` (via dependência `depends_on: minio`) utiliza o cliente `mc` da própria MinIO para autenticar, criar o bucket `trecodex-media` e aplicar permissões de leitura pública (`public`) de forma automática, economizando seu tempo.

3. **Testando via Console:**
   - Acesse no navegador: [http://localhost:9001](http://localhost:9001)
   - **Usuário**: `minioadmin`
   - **Senha**: `minioadmin`
   - Lá você poderá ver as imagens carregadas via API em tempo real.

## Variáveis de Ambiente e Configuração

No arquivo `application.yaml`, o projeto já está configurado para usar o MinIO local por padrão, mas estes valores podem ser facilmente sobrescritos com as seguintes variáveis de ambiente:

```env
STORAGE_PROVIDER=s3
STORAGE_PUBLIC_ENDPOINT=http://localhost:9000
STORAGE_BUCKET=trecodex-media
STORAGE_REGION=us-east-1
STORAGE_ACCESS_KEY=minioadmin
STORAGE_SECRET_KEY=minioadmin
STORAGE_ENDPOINT=http://localhost:9000
```

Para o ambiente de **Produção (AWS S3)**, basta retirar ou limpar as propriedades `STORAGE_ENDPOINT` e `STORAGE_PUBLIC_ENDPOINT` (elas devem ser omitidas para que o SDK use a URL automática da Amazon) e preencher `STORAGE_ACCESS_KEY` e `STORAGE_SECRET_KEY` com os dados reais do IAM.

## Preparação para Nuvens Múltiplas (GCP, Azure)

Graças ao *Dependency Inversion Principle (DIP)* e à Clean Architecture implementados, caso o projeto mude da AWS para Google Cloud ou Microsoft Azure no futuro, siga as etapas abaixo:

1. Crie uma classe (ex: `GcpStorageProvider` ou `AzureStorageProvider`) dentro de `infrastructure/storage` que implemente a interface `CloudStorageProvider`.
2. Adicione a criação condicional dessa classe no arquivo de configuração existente `StorageConfig.java` com a anotação `@ConditionalOnProperty(name = "app.storage.provider", havingValue = "gcp")`.
3. Altere a variável de ambiente para `STORAGE_PROVIDER=gcp` no seu `.env` ou orquestrador.

E pronto! **Nenhuma mudança no código de domínio ou no `MediaStorageService` será necessária**.
