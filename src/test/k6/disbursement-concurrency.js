import http from 'k6/http';

export default function () {
    const url = 'http://localhost:8080/orders';
    const payload = JSON.stringify({
        merchantReference: 'merchant-ref',
        amount: Math.random() * 50,
        createdAt: '2023-11-03'
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    http.post(url, payload, params);
}
