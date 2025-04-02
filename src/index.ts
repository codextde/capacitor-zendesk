import { registerPlugin } from '@capacitor/core';

import type { ZendeskPlugin } from './definitions';

const Zendesk = registerPlugin<ZendeskPlugin>('Zendesk', {
  web: () => import('./web').then((m) => new m.ZendeskWeb()),
});

export * from './definitions';
export { Zendesk };
