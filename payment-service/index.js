const express = require('express');

const app = express();
const SERVER_PORT = 9090;

app.use(express.json());

const payments = {
    DL123456789: {
        status: 'APPROVED',
        lastUpdateDate: '2017-07-21T17:32:28Z'
    },
    DL987654321: {
        status: 'REJECTED',
        lastUpdateDate: '2017-08-10T09:15:00Z'
    }
};

app.post('/host/credit-card-payment-api/payment-status', (req, res) => {
    const { paymentReference } = req.body;

    if (!paymentReference) {
        return res.status(400).json({
            error: 'paymentReference is required'
        });
    }

    const payment = payments[paymentReference];

    if (!payment) {
        return res.status(404).json({
            error: 'Payment not found'
        });
    }

    return res.status(200).json({
        lastUpdateDate: payment.lastUpdateDate,
        status: payment.status
    });

});

app.listen(SERVER_PORT, () => {
    console.log(`Credit card payment API running at port ${SERVER_PORT}`);
});
