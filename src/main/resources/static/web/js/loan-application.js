Vue.createApp({
    data() {
        return {
            clientInfo: {},
            errorToats: null,
            errorMsg: null
        };
    },
    methods: {
        getData: function () {
            axios.get("/api/clients/current")
                .then(response => this.clientInfo = response.data)
                .catch(() => {
                    this.errorMsg = "Error getting data";
                    this.errorToats.show();
                });
        },
        signOut: function () {
            axios.post('/api/logout')
                .then(() => window.location.href = "/web/index.html")
                .catch(() => {
                    this.errorMsg = "Sign out failed";
                    this.errorToats.show();
                });
        }
    },
    mounted: function () {
        this.errorToats = new bootstrap.Toast(document.getElementById('danger-toast'));
        this.getData();
    }
}).mount('#app');
