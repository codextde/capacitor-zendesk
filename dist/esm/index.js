import { registerPlugin } from '@capacitor/core';
const ZendeskSupport = registerPlugin('ZendeskSupport', {
    web: () => import('./web').then(m => new m.ZendeskSupportWeb()),
});
export * from './definitions';
export { ZendeskSupport };
//# sourceMappingURL=index.js.map