import React, {useEffect, useReducer, useState} from "react";
import {Badge, Card, Checkbox, Col, Collapse, Descriptions, Divider, Row, Statistic, Typography} from "antd";
import {PageHeaderWrapper} from '@ant-design/pro-layout';
import {LazyLog} from "react-lazylog";
import {Axis, Chart, Geom, Legend, Tooltip} from "bizcharts";
import ReactJson from 'react-json-view';
import moment from "moment";
import {useRequest} from "@umijs/hooks";
import request from "@/utils/request";
import {CheckboxChangeEvent} from "antd/es/checkbox";

const {Panel} = Collapse;
const {Title} = Typography;

interface TaskStatus {
  status: 'success' | 'processing' | 'default' | 'error' | 'warning';
  text: string;
}

const Dashboard: React.FC = () => {
  const url = 'https://gist.githubusercontent.com/helfi92/96d4444aa0ed46c5f9060a789d316100/raw/ba0d30a9877ea5cc23c7afcd44505dbc2bab1538/typical-live_backing.log';

  const [taskStatus, setTaskStatus] = useState<TaskStatus>({status: "processing", text: "IDLE"});

  const [lastRpsJSON, setLastRpsJSON] = useState<string>("");
  const [lastRpsData, setLastRpsData] = useState<any>("");
  const [lastResponseTimeJSON, setLastResponseTimeJSON] = useState<string>("");
  const [lastResponseTimeData, setLastResponseTimeData] = useState<any>("");
  const startTime = 1583596696795;
  const finishTime = 1583596717807;
  const timeFormat = "MM-DD HH:mm:ss";

  const rpsRequest = useRequest(({current, pageSize, sorter: s, filters: f}) => {
    // const p: any = {current, pageSize};
    // if (s?.field && s?.order) {
    //   p[s.field] = s.order;
    // }
    // if (f) {
    //   Object.entries(f).forEach(([filed, value]) => {
    //     p[filed] = value;
    //   });
    // }
    // console.log(p);
    return request.get('/api/reports/rps')
      .then(response => {
        const data = response.data;
        const dataJson = JSON.stringify(data);
        if (dataJson !== lastRpsJSON) {
          setLastRpsJSON(dataJson);
          setLastRpsData(data);
        }
      })
      ;
  }, {
    paginated: true,
    defaultPageSize: 1,
    pollingInterval: 3000,
    onError: () => {
      // setCheckboxState(false);
      rpsRequest.cancel()
    }
  });

  const responseTimeRequest = useRequest(({current, pageSize, sorter: s, filters: f}) => {
    // const p: any = {current, pageSize};
    // if (s?.field && s?.order) {
    //   p[s.field] = s.order;
    // }
    // if (f) {
    //   Object.entries(f).forEach(([filed, value]) => {
    //     p[filed] = value;
    //   });
    // }
    // console.log(p);
    return request.get('/api/reports/responseTime')
      .then(response => {
        const data = response.data;
        const dataJson = JSON.stringify(data);
        if (dataJson !== lastResponseTimeJSON) {
          setLastResponseTimeJSON(dataJson);
          setLastResponseTimeData(data);
        }
      })
      ;
  }, {
    paginated: true,
    defaultPageSize: 1,
    pollingInterval: 3000,
    onError: () => {
      // setCheckboxState(false);
      responseTimeRequest.cancel()
    }
  });

  const rpsScale = {
    time: {
      alias: "Time",
      type: "time",
      mask: "HH:mm:ss",
      tickCount: 24,
      nice: true
    },
    value: {
      alias: "RPS",
      type: "linear",
      nice: true
    },
    type: {
      type: "cat"
    }
  };

  const responseTimeScale = {
    time: {
      alias: "Time",
      type: "time",
      mask: "HH:mm:ss",
      tickCount: 24,
      nice: true
    },
    value: {
      alias: "ms",
      type: "linear",
      nice: true
    },
    type: {
      type: "cat"
    }
  };



  const json = {
    string: "this is a test ...",
    integer: 42,
    array: [1, 2, 3, 4, NaN],
    float: 3.14159,
    undefined: undefined,
    object: {
      "first-child": true,
      "second-child": false,
      "last-child": null,
    },
    "string_number": "1234",
    "date": new Date()
  };

  const json2 = {
    string: "this is a test ...",
  };

  const [rpsCheckboxState, setRpsCheckboxState] = useState(true);
  const [responseTimeCheckboxState, setResponseTimeCheckboxState] = useState(true);

  const onRpsAutoRefreshChange = (e: CheckboxChangeEvent) => {
    setRpsCheckboxState(e.target.checked);
    if (e.target.checked) {
      rpsRequest.refresh();
    } else {
      rpsRequest.cancel();
    }
  };

  const onResponseTimeAutoRefreshChange = (e: CheckboxChangeEvent) => {
    setResponseTimeCheckboxState(e.target.checked);
    if (e.target.checked) {
      responseTimeRequest.refresh();
    } else {
      responseTimeRequest.cancel();
    }
  };

  return (
    <PageHeaderWrapper>
      <Card>
          <Typography>
            <Title level={3}>
              Supervisors
            </Title>
          </Typography>
          <Row gutter={16}>
            <Col span={4}>
              <Card>
                <Statistic
                  title="Total"
                  value={20}
                  valueStyle={{fontWeight: "bold"}}
                />
              </Card>
            </Col>
            <Col span={4}>
              <Card>
                <Statistic
                  title="Ready"
                  value={20}
                  valueStyle={{fontWeight: "bold"}}
                />
              </Card>
            </Col>
            <Col span={4}>
              <Card>
                <Statistic
                  title="Occupied"
                  value={10}
                  valueStyle={{color: "#1890ff", fontWeight: "bold"}}
                />
              </Card>
            </Col>
            <Col span={4}>
              <Card>
                <Statistic
                  title="Errored"
                  value={2}
                  valueStyle={{color: '#f5222d', fontWeight: "bold"}}
                />
              </Card>
            </Col>
            <Col span={4}>
              <Card>
                <Statistic
                  title=" Running"
                  value={2}
                  valueStyle={{color: '#52c41a', fontWeight: "bold"}}
                />
              </Card>
            </Col>
            <Col span={4}>
              <Card>
                <Statistic
                  title="Idle"
                  value={2}
                  valueStyle={{fontWeight: "bold"}}
                />
              </Card>
            </Col>
          </Row>
          {/*</Typography>*/}

        <Divider/>
        <Typography>
          <Title level={3}>
            Testing Info
          </Title>
        </Typography>
        <Descriptions bordered column={3}>
          <Descriptions.Item label="Status" span={3}>
            <Badge status={taskStatus.status} text={taskStatus.text}/>
          </Descriptions.Item>
          <Descriptions.Item label="Occupied Agents">2</Descriptions.Item>
          <Descriptions.Item label="Start Time">{moment(startTime).format(timeFormat)}</Descriptions.Item>
          <Descriptions.Item label="Finish Time">{moment(finishTime).format(timeFormat)}</Descriptions.Item>
          <Descriptions.Item label="RPS Limit">300</Descriptions.Item>
          <Descriptions.Item label="Duration Limit">300</Descriptions.Item>
          <Descriptions.Item label="perAgent Limit">300</Descriptions.Item>
          <Descriptions.Item label="RPS MAX">400</Descriptions.Item>
          <Descriptions.Item label="PRS MIN">100</Descriptions.Item>
          <Descriptions.Item label="PRS AVG">250</Descriptions.Item>
          <Descriptions.Item label="PRS STDEV">300</Descriptions.Item>
          <Descriptions.Item label="PRS P90">300</Descriptions.Item>
          <Descriptions.Item label="PRS P99">300</Descriptions.Item>
          <Descriptions.Item label="Response Time MAX">400</Descriptions.Item>
          <Descriptions.Item label="Response Time MIN">100</Descriptions.Item>
          <Descriptions.Item label="Response Time AVG">250</Descriptions.Item>
          <Descriptions.Item label="Response Time STDEV">300</Descriptions.Item>
          <Descriptions.Item label="Response Time P90">300</Descriptions.Item>
          <Descriptions.Item label="Response Time P99">300</Descriptions.Item>

        </Descriptions>

        <Divider/>
        <Typography>
          <Title level={3}>
            Throughput
          </Title>
        </Typography>

        <div style={{textAlign: "right"}}>
          <Checkbox checked={rpsCheckboxState} onChange={onRpsAutoRefreshChange}>Auto Refresh</Checkbox>
        </div>

        <Chart height={400} data={lastRpsData} scale={rpsScale} forceFit>
          <Axis name="value" title/>
          <Tooltip
            // crosshairs={{
            //   type: "y"
            // }}
          />
          <Legend/>
          <Geom type="line" position="time*value" size={2}
                color={["type", ["#1890ff", "#52c41a", "#f5222d", "#faad14"]]}/>
        </Chart>

        <Divider/>
        <Typography>
          <Title level={3}>
            Response Time
          </Title>
        </Typography>

        <div style={{textAlign: "right"}}>
          <Checkbox checked={responseTimeCheckboxState} onChange={onResponseTimeAutoRefreshChange}>Auto Refresh</Checkbox>
        </div>

        <Chart height={400} data={lastResponseTimeData} scale={responseTimeScale} forceFit>
          <Axis name="value" title/>
          <Tooltip
            // crosshairs={{
            //   type: "y"
            // }}
          />
          <Legend/>
          <Geom type="line" position="time*value" size={2}
                color={["type", ["#f5222d", "#52c41a", "#1890ff"]]}/>
          {/*<Geom*/}
          {/*  type="point"*/}
          {/*  position="time*art"*/}
          {/*  size={4}*/}
          {/*  shape={"circle"}*/}
          {/*  style={{*/}
          {/*    fill: "#1890ff",*/}
          {/*    lineWidth: 1*/}
          {/*  }}*/}
          {/*/>*/}
        </Chart>

        {/*<Divider/>*/}

        {/*<Divider/>*/}
        {/*<Typography>*/}
        {/*  <Title level={3}>*/}
        {/*    Options & Config*/}
        {/*  </Title>*/}
        {/*</Typography>*/}
        <Collapse>
          <Panel header="Master Logs" key="1">
            <div style={{height: "800px"}}>
              <LazyLog extraLines={1} enableSearch url={url} caseInsensitive rowHeight={24}/>
            </div>
          </Panel>

          <Panel header="Master Options" key="2">
            <ReactJson src={json} name={false} enableClipboard={false}
                       displayDataTypes={false}/>
          </Panel>
          <Panel header="Agent Options" key="3">

            <ReactJson src={json2} name={false} enableClipboard={false}
                       displayDataTypes={false}/>
          </Panel>
          <Panel header="Agent Config" key="4">
            <a href="/">Download</a>
          </Panel>
        </Collapse>
      </Card>
    </PageHeaderWrapper>
  )
};

export default Dashboard;
