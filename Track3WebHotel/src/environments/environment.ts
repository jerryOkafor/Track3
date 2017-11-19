// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.

export const environment = {
    production: false,
    hmr: false,
    firebase: {
        apiKey: "AIzaSyCvXn_W6TcftzucZnuZjMlmCQPzsxit4og",
        authDomain: "sdg-track-3.firebaseapp.com",
        databaseURL: "https://sdg-track-3.firebaseio.com",
        projectId: "sdg-track-3",
        storageBucket: "",
        messagingSenderId: "332921740974"
    }
};
