// Copyright (c) Khaled Shawki. All rights reserved.

import BlueAlert from './BlueAlert';

interface Props {
  message: string;
}

export default function ErrorState({ message }: Props) {
  return <div className="state-card"><BlueAlert message={message} /></div>;
}
