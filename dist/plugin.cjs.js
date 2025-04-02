'use strict';

var core = require('@capacitor/core');

const Zendesk = core.registerPlugin('Zendesk', {
    web: () => Promise.resolve().then(function () { return web; }).then((m) => new m.ZendeskWeb()),
});

class ZendeskWeb extends core.WebPlugin {
    async echo(options) {
        console.log('ECHO', options);
        return options;
    }
}

var web = /*#__PURE__*/Object.freeze({
    __proto__: null,
    ZendeskWeb: ZendeskWeb
});

exports.Zendesk = Zendesk;
//# sourceMappingURL=plugin.cjs.js.map
