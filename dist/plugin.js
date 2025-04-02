var capacitorZendesk = (function (exports, core) {
    'use strict';

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

    return exports;

})({}, capacitorExports);
//# sourceMappingURL=plugin.js.map
