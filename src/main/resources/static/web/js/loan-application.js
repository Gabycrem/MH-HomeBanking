Vue.createApp({
    data() {
        return {
            clientInfo: {}, loanTypes: [], loanTypeId: 0, payments: 0,
            paymentsList: [], clientAccounts: [], accountToNumber: "VIN",
            amount: 0, fees: [], totalLoan: 0, errorToats: null,
            errorMsg: null, successMsg: null
        };
    },
    methods: {
        getData: function () {
            Promise.all([axios.get("/api/clients/current"), axios.get("/api/loans"), axios.get("/api/clients/current/accounts")])
                .then(response => {
                    this.clientInfo = response[0].data;
                    this.loanTypes = response[1].data;
                    this.clientAccounts = response[2].data;
                }).catch(() => this.showError("Error getting loan data"));
        },
        showError: function (message) {
            this.errorMsg = message;
            if (this.errorToats) this.errorToats.show();
        },
        changedType: function () {
            const selected = this.loanTypes.find(loan => loan.id == this.loanTypeId);
            this.paymentsList = selected ? selected.payments : [];
            this.payments = 0;
        },
        checkFees: function () {
            if (!this.amount || !this.payments) return this.showError("Select a loan, payments and amount first");
            this.totalLoan = Number(this.amount) * 1.20;
            const fee = this.totalLoan / Number(this.payments);
            this.fees = Array.from({ length: Number(this.payments) }, () => ({ amount: fee.toFixed(2) }));
            this.feesmodal.show();
        },
        apply: function () {
            if (!this.loanTypeId || !this.payments || this.accountToNumber === "VIN" || !this.amount) {
                return this.showError("Select a loan, payments, account and amount");
            }
            axios.post("/api/loans", { loanId: this.loanTypeId, amount: this.amount, payments: this.payments, toAccountNumber: this.accountToNumber })
                .then(() => this.okmodal.show())
                .catch(error => this.showError(error.response?.data || "Loan application failed"));
        },
        finish: function () { window.location.reload(); },
        signOut: function () {
            axios.post('/api/logout').then(() => window.location.href = "/web/index.html")
                .catch(() => this.showError("Sign out failed"));
        }
    },
    mounted: function () {
        this.errorToats = new bootstrap.Toast(document.getElementById('danger-toast'));
        this.okmodal = new bootstrap.Modal(document.getElementById('okModal'));
        this.feesmodal = new bootstrap.Modal(document.getElementById('feesModal'));
        this.getData();
    }
}).mount('#app');
