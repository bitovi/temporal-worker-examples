FROM golang:1.22.3

WORKDIR /app

COPY go.mod go.sum ./

RUN go mod download

COPY src/*.go ./

RUN CGO_ENABLED=0 GOOS=linux go build -o ./temporal-cloud-autoscaler

CMD ["./temporal-cloud-autoscaler", "--secure-port=6443", "--cert-dir=/var/run/serving-cert", "--v=10"]