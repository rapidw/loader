import React, {useEffect, useRef, useState} from "react";
import { Card, Col, Row, Statistic, Typography, Divider, Tabs, Input, Button } from "antd";
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { Line } from "@antv/g2plot";
import ReactJson from "react-json-view";
import { LazyLog } from "react-lazylog";
const { TabPane } = Tabs;


const Dashboard: React.FC = () => {
  const url = 'https://gist.githubusercontent.com/helfi92/96d4444aa0ed46c5f9060a789d316100/raw/ba0d30a9877ea5cc23c7afcd44505dbc2bab1538/typical-live_backing.log';

  const [masterOptions, setMasterOptions] = useState<any>({"test":123,"test1":"value"});
  const data = [
    { year: '1991', value: 3 },
    { year: '1992', value: 4 },
    { year: '1993', value: 3.5 },
    { year: '1994', value: 5 },
    { year: '1995', value: 4.9 },
    { year: '1996', value: 6 },
    { year: '1997', value: 7 },
    { year: '1998', value: 9 },
    { year: '1999', value: 13 },
  ];

  const container = useRef(null);
  const container2 = useRef(null);

  useEffect(() => {
    if (!container.current) {
      return;
    }
    const linePlot = new Line(container.current, {
      title: {
        visible: true,
        text: '带数据点的折线图',
      },
      description: {
        visible: true,
        text: '将折线图上的每一个数据点显示出来，作为辅助阅读。',
      },
      forceFit: true,
      padding: 'auto',
      data,
      xField: 'year',
      yField: 'value',
      point: {
        visible: true,
      },
      label: {
        visible: true,
        type: 'point',
      },
    });


    linePlot.render();
  }, []);

  useEffect(() => {
    if (!container2.current) {
      return;
    }
    const linePlot = new Line(container2.current, {
      title: {
        visible: true,
        text: '带数据点的折线图',
      },
      description: {
        visible: true,
        text: '将折线图上的每一个数据点显示出来，作为辅助阅读。',
      },
      forceFit: true,
      padding: 'auto',
      data,
      xField: 'year',
      yField: 'value',
      point: {
        visible: true,
      },
      label: {
        visible: true,
        type: 'point',
      },
    });


    linePlot.render();
  }, []);

  return (
    <PageHeaderWrapper>
      <Card>
        <div className="site-statistic-demo-card">
          {/*<Typography>*/}
          {/*  <Title level={3}>Agent Status</Title>*/}
            <Row gutter={16}>
              <Col span={4}>
                <Card>
                  <Statistic
                    title="Agent Total"
                    value={20}
                  />
                </Card>
              </Col>
              <Col span={4}>
                <Card>
                  <Statistic
                    title="Agent Ready"
                    value={20}
                  />
                </Card>
              </Col>
              <Col span={4}>
                <Card>
                  <Statistic
                    title="Agent Used"
                    value={10}
                  />
                </Card>
              </Col>
              <Col span={4}>
                <Card>
                  <Statistic
                    title="Agent Errored"
                    value={2}
                    valueStyle={{color: '#cf1322'}}
                  />
                </Card>
              </Col>
              <Col span={4}>
                <Card>
                  <Statistic
                    title="Agent Running"
                    value={2}
                    valueStyle={{color: '#3f8600'}}
                  />
                </Card>
              </Col>
              <Col span={4}>
                <Card>
                  <Statistic
                    title="Agent Idle"
                    value={2}
                  />
                </Card>
              </Col>
            </Row>
          {/*</Typography>*/}
        </div>

        <Divider />

        <Tabs style={{marginTop: "24px"}}>
          <TabPane tab="Tab 1" key="1">
            <ReactJson src={masterOptions} />
          </TabPane>
          <TabPane tab="Tab 2" key="2">
            Content of Tab Pane 2
          </TabPane>
          <TabPane tab="Tab 3" key="3">
            Content of Tab Pane 3
          </TabPane>
        </Tabs>

        <Divider />

        <div>
          <div ref={container} />
          <div ref={container2} />
        </div>

        <Divider />

        <div style={{height: "800px"}}>
          <LazyLog extraLines={1} enableSearch url={url} caseInsensitive rowHeight={24}/>
        </div>
      </Card>
    </PageHeaderWrapper>
  )
}

export default Dashboard;
