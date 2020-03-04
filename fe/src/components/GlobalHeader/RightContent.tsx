import { Tooltip, Tag } from 'antd';
import React from 'react';
import { connect } from 'dva';
import { ConnectProps, ConnectState } from '@/models/connect';
import styles from './index.less';
import { GithubOutlined } from '@ant-design/icons'
import { IconFont } from '@/utils/iconfont'

export type SiderTheme = 'light' | 'dark';
export interface GlobalHeaderRightProps extends ConnectProps {
  theme?: SiderTheme;
  layout: 'sidemenu' | 'topmenu';
}

const ENVTagColor = {
  dev: 'orange',
  test: 'green',
  pre: '#87d068',
};

const GlobalHeaderRight: React.SFC<GlobalHeaderRightProps> = props => {
  const { theme, layout } = props;
  let className = styles.right;

  if (theme === 'dark' && layout === 'topmenu') {
    className = `${styles.right}  ${styles.dark}`;
  }

  return (
    <div className={className}>
      <Tooltip title="GitHub">
        <a
          target="_blank"
          href="https://github.com/rapidw/loader"
          rel="noopener noreferrer"
          className={styles.action}
        >
          <GithubOutlined style={{fontSize:"24px"}}/>
        </a>
      </Tooltip>
      <Tooltip title="Wiki">
        <a
          target="_blank"
          href="https://github.com/rapidw/loader/wiki"
          rel="noopener noreferrer"
          className={styles.action}
        >
          <IconFont type="icon-wiki" style={{fontSize:"24px"}}/>
        </a>
      </Tooltip>
      {REACT_APP_ENV && (
        <span>
          <Tag color={ENVTagColor[REACT_APP_ENV]}>{REACT_APP_ENV}</Tag>
        </span>
      )}
      <Tooltip title="Issues">
        <a
          target="_blank"
          href="https://github.com/rapidw/loader/issues"
          rel="noopener noreferrer"
          className={styles.action}
        >
          <IconFont type="icon-issue-copy" style={{fontSize:"24px"}}/>
        </a>
      </Tooltip>
      {REACT_APP_ENV && (
        <span>
          <Tag color={ENVTagColor[REACT_APP_ENV]}>{REACT_APP_ENV}</Tag>
        </span>
      )}
    </div>
  );
};

export default connect(({ settings }: ConnectState) => ({
  theme: settings.navTheme,
  layout: settings.layout,
}))(GlobalHeaderRight);
