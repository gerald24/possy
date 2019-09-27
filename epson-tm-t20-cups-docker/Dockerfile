FROM monkeydri/cups-docker:latest

# Custom CUPS config
COPY cupsd.conf /etc/cups/cupsd.conf

# Install required libs for building CUPS drivers from source
RUN apt-get update \
  && apt-get install -y git libcups2-dev libcupsimage2-dev \
  && apt-get clean \
  && rm -rf /var/lib/apt/lists/*

# Compile and install https://github.com/nemik/epson-tm-t20-cups
RUN git clone https://github.com/nemik/epson-tm-t20-cups.git \
  && cd epson-tm-t20-cups \
  && make \
  && cp rastertozj /usr/lib/cups/filter/ \
  && mkdir -p /usr/share/cups/model/Epson \
  && cp tm20.ppd /usr/share/cups/model/Epson/
