FROM clojure:lein-2.5.3
RUN apt-get update && apt-get install -y --no-install-recommends postgresql-client && rm -rf /var/lib/apt/lists/*
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
RUN wget -O boot https://github.com/boot-clj/boot-bin/releases/download/2.5.2/boot.sh
RUN chmod 755 boot
ENV BOOT_AS_ROOT=yes
COPY boot.properties /usr/src/app/
RUN ./boot -V
RUN ./boot show --deps; exit 0
COPY build.boot /usr/src/app/
RUN ./boot show --deps; exit 0
COPY . /usr/src/app
RUN ./boot prod-build
