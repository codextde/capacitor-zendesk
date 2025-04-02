var capacitorZendesk = (function (exports, core) {
    'use strict';

    const Zendesk = core.registerPlugin('Zendesk', {
        web: () => Promise.resolve().then(function () { return web; }).then((m) => new m.ZendeskWeb()),
    });

    class ZendeskWeb extends core.WebPlugin {
        async initialize(options) {
            console.log('initialize not implemented on web yet!', options);
        }
        async setAnonymousIdentity(options) {
            console.log('setAnonymousIdentity not implemented on web yet!', options);
        }
        async setIdentity(option) {
            console.log('setIdentity not implemented on web yet!', option);
        }
        async showHelpCenter(options) {
            console.log('showHelpCenter not implemented on web yet!', options);
        }
        async showTicketRequest(options) {
            console.log('showTicketRequest not implemented on web yet!', options);
        }
        async showUserTickets() {
            console.log('showUserTickets not implemented on web yet!');
        }
    }

    var web = /*#__PURE__*/Object.freeze({
        __proto__: null,
        ZendeskWeb: ZendeskWeb
    });

    exports.Zendesk = Zendesk;

    return exports;

})({}, capacitorExports);
//# sourceMappingURL=plugin.js.map
