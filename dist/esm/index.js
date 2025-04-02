import { registerPlugin } from '@capacitor/core';
const Zendesk = registerPlugin('Zendesk', {
    web: () => import('./web').then((m) => new m.ZendeskWeb()),
});
export * from './definitions';
export { Zendesk };
//# sourceMappingURL=index.js.map